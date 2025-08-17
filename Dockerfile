FROM alpine AS builder
WORKDIR /tmp
COPY target/PloufCraft-1.0-SNAPSHOT.jar /tmp/ploufcraft.jar

FROM scratch
WORKDIR /plugins
COPY --from=builder /tmp/ploufcraft.jar /plugins/ploufcraft.jar
#COPY --from=harbor.lostaria.fr/lostaria/plugins/cosmox:prod cosmox.jar /plugins