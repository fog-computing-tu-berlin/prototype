FROM maven:3.6.1-jdk-8-alpine as edge_client

COPY pom.xml app/edge_client/
COPY src/ app/edge_client/src/
RUN cd app/edge_client/ \
    && mvn clean package \
    && mv /app/edge_client/target/edge.client.jar /app/ \
	&& rm /app/edge_client -r

FROM openjdk:8-jre-alpine
EXPOSE 5556
COPY --from=edge_client /app/ /app/
CMD ["sh", "-c", "java -jar -jar app/edge.client.jar ${Server_IP} myplant vTy \\(h,HF1\\) \\(u,xkb\\) \\(t,EKx\\) DEV"]
# (u,xkb) (t,EKx) DEV"]
#CMD ["java", "-jar", "-jar", "app/edge.client.jar", "${Server_IP}", "myplant", "vTy", "(h,HF1)", "(u,xkb)", "(t,EKx)", "DEV"]