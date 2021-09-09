#tomcat=/c/apache-tomcat-9.0.52
jar=identity-application-2.3.0.jar
javaclass=com/jfeat/identity/filter/ClientAccessMayAuthenticationFilter.class

jardir=../identity-application/target
#jardir=overlays/com.jfeat.config-application-2.3.0/WEB-INF/lib

if [ ! -d $jardir ];then
  echo No such dir: $jardir
  exit
fi

echo jar -xf $jardir/$jar $javaclass
jar -xf $jardir/$jar $javaclass
echo java -jar ~/bin/cfr-0.151.jar $javaclass
java -jar ~/bin/cfr-0.151.jar $javaclass
rm -f $javaclass
