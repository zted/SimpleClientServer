FROM centos:7
MAINTAINER Ted Zhang <tedz.cs@gmail.com>

RUN yum update -y && \
    yum install epel-release -y && \
    yum install -y java-1.8.0-openjdk-devel  && \
    yum clean all -y

# Define commonly used JAVA_HOME variable
ENV JAVA_HOME /usr/lib/jvm/java-8-oracle

COPY target /opt/target
COPY server.sh /opt/server.sh
COPY client.sh /opt/client.sh

CMD cd /opt && \
    bash client.sh
