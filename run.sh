#!/bin/bash
# Copyright 2020 Google LLC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

set -e
mvn package
cp target/cloud-spanner-commit-timestamp-1.0-SNAPSHOT.jar target/alternateLocation/
export PROJECT_ID=
export INSTANCE_ID=
export DB_NAME=
export GOOGLE_APPLICATION_CREDENTIALS=
if [ -z "$PROJECT_ID" -o -z "$INSTANCE_ID" -o -z "$DB_NAME" -o -z "$GOOGLE_APPLICATION_CREDENTIALS" ];
then
  cat <<EOF
[ERROR] Setup env PROJECT_ID INSTANCE_ID DB_NAME and GOOGLE_APPLICATION_CREDENTIALS
[ERROR] For GOOGLE_APPLICATION_CREDENTIALS, generate a file containing the credentials,
[ERROR] follow this link:
[ERROR] https://cloud.google.com/spanner/docs/reference/libraries#setting_up_authentication
[ERROR] to generate.
EOF
  exit 1
fi
java -cp $(ls -1  target/alternateLocation/*.jar|tr '\n' ':')  example.SpannerExample
