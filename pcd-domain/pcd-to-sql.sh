#!/bin/bash
#set -x
#http://www.mca.gov.cn/article/sj/xzqh/2019/
#
# 需要先手动去掉 台湾/香港/澳门

id=0
preProvinceCode=''
preProvinceName=''
provinceId=''
cityId=''
currentLine=0
totalLine=`sed -n '$=' pcd.txt `

echo "SET FOREIGN_KEY_CHECKS=0;"
echo "delete from t_pcd;"

echo -n "insert into t_pcd values "
while read line; do
  currentLine=`expr $currentLine + 1`
  id=`expr $id + 1`
  code=`echo $line | awk '{print$1}'`
  name=`echo $line | awk '{print$2}'`
  if [[ $code =~ [0-9]{2}0000 ]]; then
    preProvinceCode=$code
    preProvinceName=$name
    provinceId=$id
    echo -n "($id, '$name', 'p', NULL)"
  elif [[ $code =~ [0-9]{4}00 ]]; then
    # city
    echo -n "($id, '$name', 'c', $provinceId)"
    cityId=$id
  elif [[ $code =~ [0-9]{2}0101 ]]; then
    # the first district
    if [[ $preProvinceCode =~ [0-9]{2}0000 ]]; then
      echo -n "($id, '$preProvinceName', 'c', $provinceId),"
      cityId=$id
      id=`expr $id + 1`
    fi
    echo -n "($id, '$name', 'd', $cityId)"
  else
    # district
    echo -n "($id, '$name', 'd', $cityId)"
  fi

  if [[ $currentLine != $totalLine ]]; then
    echo -n ","
  else
    echo -n ";"
  fi
done < pcd.txt
