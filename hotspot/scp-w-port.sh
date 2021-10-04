INPUT=/Users/vincenthuang/workspace/alliance/biliya/product-domain/target/classes/com/jfeat/product/model/ProductSettlementProportion.class

HOST=root@a.f.smallsaas.cn
TARGET=/home/biliya/mall-webapp/webapps/classes


scp -P 7022 $INPUT $HOST:$TARGET
