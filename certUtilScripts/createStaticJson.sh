if [ -z "$DOMAIN_URL" ]
then
    echo "Domain URL is not set"
    exit 
fi

rm -rf out
mkdir tempOut
cp -R outTemplate/* tempOut
cd tempOut
find . -type f -exec sed -i.bak -e "s/localhost/$DOMAIN_URL/g" {} \;
find . -type f -name "*.json" -exec sed -i.bak2 -e "s/container/$CONTAINER_NAME/g" {} \;
find . -type f -name "*.json.bak*" -exec rm -f {} \;
cd ..
mv tempOut out
echo "Done."
