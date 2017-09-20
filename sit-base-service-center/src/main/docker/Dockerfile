FROM java:8
VOLUME /tmp
ADD sit-base-service-center-0.0.1.jar app.jar
RUN bash -c 'touch /app.jar'
EXPOSE 9099
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
