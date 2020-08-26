tomcatImage='daocloud.io/library/tomcat:7-jre8'
docker pull $tomcatImage

echo "============ viewing tomcat 7 images"
docker images


echo "============ stoping mall-webapp"
docker stop mall-webapp

echo "============ deleting mall-webapp"
docker rm mall-webapp

echo "============ starting mall-webapp"
docker run --name mall-webapp  -d --privileged=true \
  -e TZ="Asia/Shanghai" \
  -v /etc/localtime:/etc/localtime:ro \
  -v ${PWD}/logs:/usr/local/tomcat/logs \
  -v ${PWD}/config/setenv.sh:/usr/local/tomcat/bin/setenv.sh \
  -v ${PWD}/config:/usr/local/tomcat/config \
  -v ${PWD}/webapps:/usr/local/tomcat/webapps \
  -v ${PWD}/images:/images \
  --link mall-redis:redis \
  -p 8081:8080 $tomcatImage


docker ps
