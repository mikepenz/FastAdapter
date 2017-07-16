#!/bin/bash

if [ "$1" = "release" ];
then
    ./gradlew clean build uploadArchives generatePomFileForReleasePublication bintrayUpload Plibrarycoreonly
    ./gradlew build uploadArchives generatePomFileForReleasePublication bintrayUpload -x test -Plibrarycommonsonly
    ./gradlew build uploadArchives generatePomFileForReleasePublication bintrayUpload -x test -Plibraryextensiononly
    ./gradlew build uploadArchives generatePomFileForReleasePublication bintrayUpload -x test -libraryextensionexpandablemonly
else
    ./gradlew clean build uploadArchives -Plibrarycoreonly
    ./gradlew build uploadArchives -x test -Plibrarycommonsonly
    ./gradlew build uploadArchives -x test -Plibraryextensiononly
    ./gradlew build uploadArchives -x test -libraryextensionexpandablemonly
fi