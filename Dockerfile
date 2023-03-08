FROM ghcr.io/graalvm/graalvm-ce:ol9-java11-22.3.1
RUN gu install ruby && /opt/graalvm-ce-java11-22.3.1/languages/ruby/lib/truffle/post_install_hook.sh
RUN microdnf install git unzip zip -y
WORKDIR /tmp/
# now install protoc because the version from the repos is too old
# https://github.com/protocolbuffers/protobuf/issues/11935
RUN [[ $(uname -i) == aarch64* ]] && curl -OL https://github.com/protocolbuffers/protobuf/releases/download/v22.0/protoc-22.0-linux-aarch_64.zip; exit 0
RUN [[ $(uname -i) == x86_64* ]] && curl -OL https://github.com/protocolbuffers/protobuf/releases/download/v22.0/protoc-22.0-linux-x86_64.zip; exit 0
RUN unzip protoc*.zip
RUN mv ./bin/protoc /usr/bin/
RUN protoc --version
RUN git clone https://github.com/Transfusion/protobuf.git
WORKDIR /tmp/protobuf
RUN git checkout v3.21.12-no-lto
RUN cd ruby/ && bundle && rake && rake clobber_package gem && gem install `ls pkg/google-protobuf-*.gem`
RUN gem install app-info -v 2.8.3
#https://stackoverflow.com/questions/22111060/what-is-the-difference-between-expose-and-publish-in-docker
EXPOSE 8080
ARG JAR_FILE=./build/libs/deployapp-storage-service.jar
WORKDIR /app
COPY ${JAR_FILE} app.jar
RUN ls /app
RUN java --version
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app/app.jar ${0} ${@}"]