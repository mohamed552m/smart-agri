# Android App

Single-module Kotlin and Jetpack Compose application. Shared domain models and
repository interfaces live under `app/src/main/java/com/smartagri/app/core/`.
Feature owners must wait for this foundation to merge, then work only in their
assigned feature packages.

Firebase dependencies are declared, but Google Services is intentionally not
applied until an approved private `google-services.json` and Firebase project
exist. No cloud setup is performed by the foundation.
