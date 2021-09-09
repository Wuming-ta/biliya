tomcat=/c/apache-tomcat-9.0.52
war=ROOT.war
jar=identity-application-2.3.0.jar
javaclass=com/jfeat/identity/filter/ClientAccessMayAuthenticationFilter.class

if [ ! -d $tomcat/tmp ];then
  mkdir $tomcat/tmp
fi
cd $tomcat/tmp
rm -rf $tomcat/tmp/ROOT.war WEB-INF com
echo cp $tomcat/webapps/ROOT.war .
cp $tomcat/webapps/ROOT.war .

echo jar -xf $war WEB-INF/lib/$jar
jar -xf $war WEB-INF/lib/$jar
ls WEB-INF/lib/$jar
echo done
echo jar -xf WEB-INF/lib/$jar $javaclass
jar -xf WEB-INF/lib/$jar $javaclass
echo java -jar ~/bin/cfr-0.151.jar $javaclass
java -jar ~/bin/cfr-0.151.jar $javaclass
