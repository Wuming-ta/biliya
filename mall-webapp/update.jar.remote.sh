tomcat=/c/apache-tomcat-9.0.52
jar=identity-application-2.3.0.jar
#jardir=../identity-application/target
jardir=../identity-application/target

if [ ! -d $jardir ];then
  echo No such dir: $jardir
  exit
fi
if [ ! -d $tomcat/webapps/WEB-INF/lib ];then
   mkdir -p $tomcat/webapps/WEB-INF/lib
fi

rm -f $tomcat/webapps/ROOT/WEB-INF/lib/$jar
echo cp $jardir/$jar $tomcat/webapps/WEB-INF/lib
cp $jardir/$jar $tomcat/webapps/WEB-INF/lib

cd $tomcat/webapps
echo jar -0uf ROOT.war WEB-INF/lib/$jar
jar -0uf ROOT.war WEB-INF/lib/$jar
echo done
rm -rf WEB-INF
