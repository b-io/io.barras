#!/bin/sh

cd nconnect/
rm -rf dist
rm -rf target
sh install
poetry publish --build
cd ..

cd nfin/
rm -rf dist
rm -rf target
sh install
poetry publish --build
cd ..

cd nformat/
rm -rf dist
rm -rf target
sh install
poetry publish --build
cd ..

cd ngui/
rm -rf dist
rm -rf target
sh install
poetry publish --build
cd ..

cd nlearn/
rm -rf dist
rm -rf target
sh install
poetry publish --build
cd ..

cd nmath/
rm -rf dist
rm -rf target
sh install
poetry publish --build
cd ..

cd nserve/
rm -rf dist
rm -rf target
sh install
poetry publish --build
cd ..

cd nutil/
rm -rf dist
rm -rf target
sh install
poetry publish --build
cd ..
