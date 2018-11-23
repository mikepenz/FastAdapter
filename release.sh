#!/bin/bash

./gradlew clean build

if [ "$1" = "release" ];
then
    ./gradlew library-core:bintrayUpload -Plibrary_extensions_core_only
    ./gradlew library-extensions-diff:bintrayUpload -x test -Plibrary_extensions_diff_only
    ./gradlew library-extensions-drag:bintrayUpload -x test -Plibrary_extensions_drag_only
    ./gradlew library-extensions-expandable:bintrayUpload -x test -Plibrary_extensions_expandable_only
    ./gradlew library-extensions-scroll:bintrayUpload -x test -Plibrary_extensions_scroll_only
    ./gradlew library-extensions-swipe:bintrayUpload -x test -Plibrary_extensions_swipe_only
    ./gradlew library-extensions-ui:bintrayUpload -x test -Plibrary_extensions_ui_only
    ./gradlew library-extensions-utils:bintrayUpload -x test -Plibrary_extensions_utils_only
else
    //TODO
fi
