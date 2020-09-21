# updateConfig.sh
#
# @brief 	Upload the config.json file to S3 that controls the content available in Borzoi
# @author 	@atomicfruitcake
# @date 	2020
echo 'Uploading config.json file to S3' 
gzip -f -k ./config.json
aws s3 cp ./config.json.gz s3://wirewax/borzoi/config.json --acl public-read --cache-control no-cache --content-encoding gzip
rm ./config.json.gz
echo 'Upload of config.json file is complete'
