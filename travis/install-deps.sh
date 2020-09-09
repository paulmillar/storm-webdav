#!/bin/bash
#
# Copyright (c) Istituto Nazionale di Fisica Nucleare, 2014-2020.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

set -ex

pwd

sudo apt-get install -y wget
wget https://raw.githubusercontent.com/italiangrid/build-settings/master/maven/cnaf-mirror-settings.xml

mv cnaf-mirror-settings.xml ~/.m2/settings.xml
