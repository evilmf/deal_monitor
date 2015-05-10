rmdir /s /q G:\apache-tomcat-7.0.57\webapps\af\
del g:\apache-tomcat-7.0.57\webapps\af.war
del /F/ Q g:\apache-tomcat-7.0.57\logs\*
del /F/ Q g:\apache-tomcat-7.0.57\conf\af.properties

copy G:\git\af\src\main\config\af.properties g:\apache-tomcat-7.0.57\conf\
copy G:\git\af\target\af.war g:\apache-tomcat-7.0.57\webapps\
g:\apache-tomcat-7.0.57\bin\startup.bat