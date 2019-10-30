FROM openjdk:8-jdk
VOLUME /tmp
COPY target/${JAR_FILE} toutiao.jar
COPY src/main/resources/elastic-jtdn-certificates-* /tmp/
RUN yum install -y tzdata \
    && cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
    && dpkg-reconfigure -f noninteractive tzdata

ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /toutiao.jar