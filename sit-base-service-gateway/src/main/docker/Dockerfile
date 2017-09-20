FROM java:8
VOLUME /tmp
ADD sit-base-service-gateway-0.0.1.jar app.jar
RUN bash -c 'touch /app.jar'
EXPOSE 9098
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
