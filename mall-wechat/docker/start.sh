tomcatImage='daocloud.io/library/tomcat:7-jre8'
docker pull $tomcatImage

echo "============ viewing tomcat 7 images"
docker images


echo "============ stoping mall-wx"
docker stop mall-wx

echo "============ deleting mall-wx"
docker rm mall-wx

echo "============ starting mall-wx"
docker run --name mall-wx  -d --privileged=true \
  -e TZ="Asia/Shanghai" \
  -v /etc/localtime:/etc/localtime:ro \
  -v ${PWD}/logs:/usr/local/tomcat/logs \
  -v ${PWD}/config/setenv.sh:/usr/local/tomcat/bin/setenv.sh \
  -v ${PWD}/config:/usr/local/tomcat/config \
  -v ${PWD}/webapps:/usr/local/tomcat/webapps \
  -p 8080:8080 $tomcatImage

docker ps
