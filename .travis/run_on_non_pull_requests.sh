#!/bin/bash
openssl aes-256-cbc -K $encrypted_b7f76037f2f7_key -iv $encrypted_b7f76037f2f7_iv -in $TRAVIS_BUILD_DIR/SWADroid/src/prod/google-services.json.enc -out $TRAVIS_BUILD_DIR/SWADroid/src/prod/google-services.json -d
