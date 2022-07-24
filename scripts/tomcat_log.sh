JAVA_OPTS="${JAVA_OPTS} -Dserver.tomcat.accesslog.enabled=true"
JAVA_OPTS="${JAVA_OPTS} -Dserver.tomcat.basedir=."

nohup java ${JAVA_OPTS} -jar /home/ec2-user/app/step1/*.jar --logging.file.path=/home/ec2-user/app/step1/
 --logging.level.org.hibernate.SQL=DEBUG >> /home/ec2-user/app/step1/deploy.log 2>/home/ec2-user/app/step1/deploy_err.log &