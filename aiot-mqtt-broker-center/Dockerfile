FROM cs-harbor.sunvalley.com.cn/library/centos7-java11-base:v1.1.0
##### custom
MAINTAINER kai "kai.li@sunvalley.com.cn"
ENV SERVER_NAME=aiot-mqtt-broker-center
ENV MQTT_PORT=9901
ENV HTTP_PORT=9902

###### standard
COPY target/${SERVER_NAME}.jar /data/project/
EXPOSE ${MQTT_PORT} ${HTTP_PORT}

RUN curl --create-dirs -s -o /data/script/server/deploy.sh http://cs-scripts.nearbyexpress.com:81/deploy.sh && \
   chmod +x /data/script/server/deploy.sh

VOLUME ["/data/volume"]
CMD ["/data/script/server/deploy.sh"]
