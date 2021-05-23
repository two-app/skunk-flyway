local_env:
	docker-compose up -d postgres

local_env_down:
	docker-compose rm --force --stop -v postgres

test: clean_localstack
	docker-compose build test
	docker-compose run --rm test

