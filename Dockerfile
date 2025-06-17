# 1. Usar una imagen base oficial que ya tiene Java 17 y Tomcat 9 instalados.
FROM tomcat:9.0-jdk17-temurin

# 2. Opcional pero recomendado: Limpiar las aplicaciones de ejemplo de Tomcat.
RUN rm -rf /usr/local/tomcat/webapps/*

# 3. Copiar tu archivo .war a la carpeta de despliegue de Tomcat.
#    Esta línea ya está corregida con el nombre de tu archivo.
COPY dist/VLSM.war /usr/local/tomcat/webapps/ROOT.war

# 4. Exponer el puerto 8080, que es donde escucha Tomcat.
EXPOSE 8080

# 5. El comando para iniciar el servidor Tomcat.
CMD ["catalina.sh", "run"]