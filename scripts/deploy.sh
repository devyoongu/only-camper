#!/bin/bash

REPOSITORY=/home/ec2-user/app/step2
PROJECT_NAME=only-camper

echo "> Build 파일 복사"

cp $REPOSITORY/zip/*.jar $REPOSITORY/

echo "> 현재 구동중인 애플리케이션 pid 확인"

CURRENT_PID=$(pgrep -fl only-camper | grep jar | awk '{print $1}')

echo "현재 구동중인 어플리케이션 pid: $CURRENT_PID"

#실행중인 8080포트 죽이기
echo "> 8080 port kill"
fuser 8080/tcp
fuser -k 8080/tcp

if [ -z "$CURRENT_PID" ]; then
    echo "> 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다."
else
    echo "> kill -15 $CURRENT_PID"
    kill -15 $CURRENT_PID
    sleep 5
fi

echo "> 새 어플리케이션 배포"

JAR_NAME=$(ls -tr $REPOSITORY/*.jar | tail -n 1)

echo "> JAR Name: $JAR_NAME"

echo "> $JAR_NAME 에 실행권한 추가"

chmod +x $JAR_NAME

echo "> $JAR_NAME 실행"

#nohup java -jar \
#    -Dspring.config.location=classpath:/application.yml,classpath:/application-real.yml,classpath:/application-oauth.properties \
#    -Dspring.profiles.active=real \
#    $JAR_NAME > $REPOSITORY/nohup.out 2>&1 &

nohup java -jar \
    -Dspring.config.location=classpath:/application.yml,classpath:/application-real.yml,/home/ec2-user/app/application-oauth.properties,/home/ec2-user/app/application-real-db.yml \
    -Dspring.profiles.active=real \
    $JAR_NAME > $REPOSITORY/nohup.out 2>&1 &
