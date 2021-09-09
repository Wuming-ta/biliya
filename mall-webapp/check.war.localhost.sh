#tomcat=/c/apache-tomcat-9.0.52
war=mall-webapp-2.3.0.war
jar=identity-application-2.3.0.jar
javaclass=com/jfeat/identity/filter/ServletUtils.class

echo jar -xf target/$war $jar
jar -xf target/$war WEB-INF/lib/$jar
ls WEB-INF/lib/$jar
echo done
echo jar -xf WEB-INF/lib/$jar $javaclass
jar -xf WEB-INF/lib/$jar $javaclass
echo java -jar ~/bin/cfr-0.151.jar $javaclass
java -jar ~/bin/cfr-0.151.jar $javaclass
