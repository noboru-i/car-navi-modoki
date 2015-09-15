#!/usr/bin/env bash

sudo apt-get install jq

UPLOAD_URL=`curl -H "Authorization: token $GITHUB_ACCESS_TOKEN" https://api.github.com/repos/noboru-i/car-navi-modoki/releases/tags/$CIRCLE_TAG | jq '.upload_url'`
UPLOAD_URL=`echo $UPLOAD_URL | sed -e 's/{?name,label}/?name=/g' | sed -e 's/"//g'`

curl --verbose --data-binary @$1 -H "Content-Type: application/zip" -H "Authorization: token $GITHUB_ACCESS_TOKEN" $UPLOAD_URL$2
