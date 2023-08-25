# quic_test

Приложение для проверки протокола quic
https://ru.wikipedia.org/wiki/QUIC


## Добавление библиотек в локальный репозиторий
```
mvn deploy:deploy-file -DgroupId=net.luminis -DartifactId=flupke -Dversion=1.0.0 -Durl=file:./local-maven-re
po/ -DrepositoryId=local-maven-repo -DupdateReleaseInfo=true -Dfile=./lib/flupke-plugin.jar
```

```
mvn deploy:deploy-file -DgroupId=net.luminis -DartifactId=kwik -Dversion=1.0.0 -Durl=file:./local-maven-repo
/ -DrepositoryId=local-maven-repo -DupdateReleaseInfo=true -Dfile=./lib/kwik.jar
```

Исходники библиотек

https://github.com/ptrd/kwik

https://github.com/ptrd/flupke

Для теста можно сгенерировать ключи

```sudo sh generate-tls-certificates.sh```

