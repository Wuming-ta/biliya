tomcat=/c/apache-tomcat-9.0.52
war=mall-webapp-2.3.0.war
jar=identity-application-2.3.0.jar
javaclass=com/jfeat/identity/filter/ServletUtils.class

#cd ../identity-application/
#echo mvn clean install
#mvn clean install
#
#cd ../mall-webapp
#cp ../identity-application/target/identity-application-2.3.0.jar overlays/com.jfeat.config-application-2.3.0/WEB-INF/lib/identity-application-2.3.0.jar
#echo mvn clean package
#mvn clean package


rm -rf $tomcat/webapps/ROOT.war
rm -rf $tomcat/webapps/ROOT
echo cp target/$war $tomcat/webapps/ROOT.war
cp target/$war $tomcat/webapps/ROOT.war
