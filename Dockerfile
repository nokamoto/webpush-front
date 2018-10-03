FROM nokamoto13/geckodriver-scala:0.0.0

ARG VERSION
ARG APP=webpush-front

COPY target/universal/${APP}-${VERSION}.tgz .

RUN tar -zxvf ${APP}-${VERSION}.tgz && mv ${APP}-${VERSION} ${APP}

RUN rm ${APP}-${VERSION}.tgz

ENTRYPOINT [ "webpush-front/bin/webpush-front" ]
