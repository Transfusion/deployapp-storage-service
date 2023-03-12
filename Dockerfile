FROM transfusion/deployapp-base-container:ol9-java11-22.3.1-app-info-2.8.3
RUN gem install app-info -v 2.8.3
#https://stackoverflow.com/questions/22111060/what-is-the-difference-between-expose-and-publish-in-docker
EXPOSE 8080
ARG JAR_FILE=./build/libs/deployapp-storage-service.jar
WORKDIR /app
COPY ${JAR_FILE} app.jar
RUN ls /app
RUN java --version
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app/app.jar ${0} ${@}"]