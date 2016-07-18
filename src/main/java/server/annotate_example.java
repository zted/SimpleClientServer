package server;

import edu.jhu.hlt.concrete.*;
import edu.jhu.hlt.concrete.search.SearchQuery;
import edu.jhu.hlt.concrete.search.SearchResults;
import edu.jhu.hlt.concrete.services.Annotator;
import edu.jhu.hlt.concrete.uuid.AnalyticUUIDGeneratorFactory;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by ted on 6/23/16.
 */
public class annotate_example {
    public static void main(String[] args) throws TException {
        /**
         * Runs a dummy example of concretizing a tweet, and sending it to
         * multiple docker servers that implement the "annotate" method.
         * The docker servers can be found from docker-nltk
         */

        AnalyticUUIDGeneratorFactory f = new AnalyticUUIDGeneratorFactory();
        AnalyticUUIDGeneratorFactory.AnalyticUUIDGenerator gen = f.create();

        // Create a communication and set some required fields
        Communication comm = new Communication();
        comm.setId("SomeID");
        comm.setUuid(gen.next());
        comm.setType("EnglishQuery");
        AnnotationMetadata metadata = new AnnotationMetadata();
        metadata.setTool("SomeTool");
        comm.setMetadata(metadata);
        String someText = "I like fruits. Do not stop me from eating them.";
        comm.setOriginalText(someText);
        comm.setText(someText);

        // Sentences must exist for section
        Section sec = new Section(gen.next(), "SomeSection");
        TextSpan ts = new TextSpan(0, someText.length());
        sec.setTextSpan(ts);

        Sentence sent = new Sentence(gen.next());
        sent.setTextSpan(ts);
        sec.addToSentenceList(sent);
        comm.addToSectionList(sec);

        Scanner input = new Scanner(System.in);
        System.out.println("Please enter in the hostname:");
        String inputString = input.nextLine();
        String host = inputString;
        List<Integer> arrList = new ArrayList<>();

        while(true) {
            System.out.println("Please enter a port here, type exit to quit: ");

            inputString = input.nextLine();
            if (inputString.toLowerCase().equals("exit")) {
                break;
            }
            arrList.add((Integer.parseInt(inputString)));
        }

        for (Integer i : arrList) {
            TTransport transport = new TSocket(host, i);
            transport = new TFramedTransport(transport);
            transport.open();
            System.out.println("Port opened!");
            TCompactProtocol protocol = new TCompactProtocol(transport);
            Annotator.Client client = new Annotator.Client(protocol);
            comm = client.annotate(comm);
            transport.close();
        }

//        comm = convertArcsToTokens(comm);
        //*/

        System.out.println(comm);
    }

    public static Communication convertArcsToTokens(Communication translatedComm) {
        TextSpan ts;
        Communication newComm = new Communication();

        AnalyticUUIDGeneratorFactory f = new AnalyticUUIDGeneratorFactory();
        AnalyticUUIDGeneratorFactory.AnalyticUUIDGenerator gen = f.create();

        // Create a communication and set some required fields
        newComm.setId("SomeID");
        newComm.setUuid(gen.next());
        newComm.setType("TranslatedQuery");
        AnnotationMetadata metadata = new AnnotationMetadata();
        metadata.setTool("SomeTool");
        newComm.setMetadata(metadata);
        newComm.setOriginalText(translatedComm.getOriginalText());

        List<EntityMention> eml = new ArrayList<>();
        EntityMentionSet ems = new EntityMentionSet(gen.next(), metadata, eml);

        List<Entity> esl = new ArrayList<>();
        EntitySet es = new EntitySet(gen.next(), metadata, esl);

        newComm.addToEntityMentionSetList(ems);
        newComm.addToEntitySetList(es);

        String documentStr = new String("");
        int currentDocumentLength = 0;

        for (Section sec : translatedComm.getSectionList()) {
            String sectionStr = new String("");
            Section newSec = new Section(sec.getUuid(), sec.getKind());
            newComm.addToSectionList(newSec);
            int currentSectionLength = currentDocumentLength;

            for (Sentence sent : sec.getSentenceList()) {
                String sentenceStr = new String("");
                Sentence newSent = new Sentence(gen.next());
                newSent.setTextSpan(sent.getTextSpan());
                AnnotationMetadata md = new AnnotationMetadata();
                md.setTool("keyword_translation");
                md.setTimestamp(System.currentTimeMillis());
                // TODO: do we really want to use this timestamp?

                Tokenization newTok = new Tokenization(gen.next(), md, sent.getTokenization().getKind());
                TokenList tokList = new TokenList(sent.getTokenization().getLattice().getCachedBestPath().getTokenList());
                newTok.setTokenList(tokList);
                newSent.setTokenization(newTok);
                newSec.addToSentenceList(newSent);

                int currentSentenceLength = currentSectionLength;
                for (Token t : tokList.getTokenList()) {
                    ts = new TextSpan(currentSentenceLength, currentSentenceLength + t.getText().length());
                    // this is the text span for the token
                    currentSentenceLength += t.getText().length();
                    sentenceStr += t.getText();
                    t.setTextSpan(ts);
                }

                ts = new TextSpan(currentSectionLength, currentSectionLength + sentenceStr.length());
                // this is the text span for the sentence
                currentSectionLength += sentenceStr.length();
                sectionStr += sentenceStr;
                newSent.setTextSpan(ts);
            }

            ts = new TextSpan(currentDocumentLength, currentDocumentLength + sectionStr.length());
            // this is the text span for the section
            currentDocumentLength += sectionStr.length();
            documentStr += sectionStr;
            newSec.setTextSpan(ts);
        }

        newComm.setText(documentStr);

        return newComm;
    }

}
