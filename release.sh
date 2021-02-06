#!/bin/bash

./gradlew clean build

if [ "$1" = "release" ];
then
    ./gradlew fastadapter:publishReleasePublicationToSonatypeRepository -Plibrary_core_only
    ./gradlew fastadapter-extensions-binding:publishReleasePublicationToSonatypeRepository -x test -x lint -Plibrary_extensions_binding_only
    ./gradlew fastadapter-extensions-diff:publishReleasePublicationToSonatypeRepository -x test -x lint -Plibrary_extensions_diff_only
    ./gradlew fastadapter-extensions-drag:publishReleasePublicationToSonatypeRepository -x test -x lint -Plibrary_extensions_drag_only
    ./gradlew fastadapter-extensions-expandable:publishReleasePublicationToSonatypeRepository -x test -x lint -Plibrary_extensions_expandable_only
    ./gradlew fastadapter-extensions-paged:publishReleasePublicationToSonatypeRepository -x test -x lint -Plibrary_extensions_paged_only
    ./gradlew fastadapter-extensions-scroll:publishReleasePublicationToSonatypeRepository -x test -x lint -Plibrary_extensions_scroll_only
    ./gradlew fastadapter-extensions-swipe:publishReleasePublicationToSonatypeRepository -x test -x lint -Plibrary_extensions_swipe_only
    ./gradlew fastadapter-extensions-ui:publishReleasePublicationToSonatypeRepository -x test -x lint -Plibrary_extensions_ui_only
    ./gradlew fastadapter-extensions-utils:publishReleasePublicationToSonatypeRepository -x test -x lint -Plibrary_extensions_utils_only
else
    //TODO
fi
