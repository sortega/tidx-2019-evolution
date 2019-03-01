product-visit-v0.avro: product-visit-v0.avsc product-visit-v0.json
	avro-tools fromjson --codec snappy --schema-file product-visit-v0.avsc product-visit-v0.json > product-visit-v0.avro

show-product-visit-v0: product-visit-v0.avro
	avro-tools tojson --pretty product-visit-v0.avro

product-visit-v1.avro: product-visit-v1.avsc product-visit-v1.json
	avro-tools fromjson --codec snappy --schema-file product-visit-v1.avsc product-visit-v1.json > product-visit-v1.avro

show-product-visit-v1: product-visit-v1.avro
	avro-tools tojson --pretty product-visit-v1.avro

emit-product-visit-v0-wrong:
	kafka-avro-console-producer --topic product-visit \
	    --broker-list localhost:9092 \
	    --property schema.registry.url=http://localhost:8081 \
	    --property value.schema="`cat product-visit-v0-wrong.avsc | tr -d '\n\r'`"

emit-product-visit-v0: product-visit-v0.avsc
	kafka-avro-console-producer --topic product-visit \
	    --broker-list localhost:9092 \
	    --property schema.registry.url=http://localhost:8081 \
	    --property value.schema="`cat product-visit-v0.avsc | tr -d '\n\r'`"

emit-product-visit-v1: product-visit-v1.avsc
	kafka-avro-console-producer --topic product-visit \
	    --broker-list localhost:9092 \
	    --property schema.registry.url=http://localhost:8081 \
	    --property value.schema="`cat product-visit-v1.avsc | tr -d '\n\r'`"

consume-product-visit:
	kafka-avro-console-consumer --topic product-visit \
	    --bootstrap-server localhost:9092 \
	    --property schema.registry.url=http://localhost:8081 \
	    --from-beginning

clean:
	rm -f product-visit-v{0,1}.avro
	curl -X DELETE http://localhost:8081/subjects/product-visit-value
	kafka-topics --zookeeper localhost:2181 --topic product-visit --delete --if-exists
