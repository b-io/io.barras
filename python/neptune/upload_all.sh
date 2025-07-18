cd nfin/
rm -rf dist
rm -rf target
sh install
#twine upload dist/*
cd ..

cd ngui/
rm -rf dist
rm -rf target
sh install
#twine upload dist/*
cd ..

cd nlearn/
rm -rf dist
rm -rf target
sh install
#twine upload dist/*
cd ..

cd nmath/
rm -rf dist
#rm -rf target
sh install
#twine upload dist/*
cd ..

cd nutil/
rm -rf dist
rm -rf target
sh install
#twine upload dist/*
cd ..
