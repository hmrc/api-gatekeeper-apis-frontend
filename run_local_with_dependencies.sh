#!/bin/bash

sm2 -start DATASTREAM AUTH AUTH_LOGIN_API AUTH_LOGIN_STUB TIME_BASED_ONE_TIME_PASSWORD STRIDE_AUTH_FRONTEND STRIDE_AUTH STRIDE_IDP_STUB USER_DETAILS

./run_local.sh
