tomcat=/c/apache-tomcat-9.0.52
war=mall-webapp-2.3.0.war
wardir=mall-webapp/target

cp $wardir/$war $tomcat/webapps/ROOT.war
rm -rf $tomcat/webapps/ROOT
