#!/bin/bash

./gradlew clean build

if [ "$1" = "release" ];
then
    ./gradlew library-core:bintrayUpload -Plibrarycoreonly
    ./gradlew library:bintrayUpload -x test -Plibrarycommonsonly
    ./gradlew library-extensions:bintrayUpload -x test -Plibraryextensiononly
    ./gradlew library-extensions-expandable:bintrayUpload -x test -Plibraryextensionexpandablemonly
else
    //TODO
fi