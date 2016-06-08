rmdir /s /q E:\apache-tomcat-7.0.57\webapps\ROOT\
del e:\apache-tomcat-7.0.57\webapps\af.war
del /F/ Q e:\apache-tomcat-7.0.57\logs\*
del /F/ Q e:\apache-tomcat-7.0.57\conf\af.properties

copy E:\git\af\src\main\config\af.properties e:\apache-tomcat-7.0.57\conf\
copy E:\git\af\target\af.war e:\apache-tomcat-7.0.57\webapps\
e:\apache-tomcat-7.0.57\bin\startup.bat