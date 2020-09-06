#!/bin/bash
penssl aes-256-cbc -K $encrypted_855ad244b8b2_key -iv $encrypted_855ad244b8b2_iv -in keystore.jks.enc -out keystore.jks -d
