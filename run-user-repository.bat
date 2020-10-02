@REM
@REM Copyright 2019 European Commission
@REM
@REM Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
@REM You may not use this work except in compliance with the Licence.
@REM You may obtain a copy of the Licence at:
@REM
@REM     https://joinup.ec.europa.eu/software/page/eupl
@REM
@REM Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
@REM WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@REM See the Licence for the specific language governing permissions and limitations under the Licence.
@REM

@echo off
TITLE User Repository
echo "---------------------USER REPOSITORY-----------------------------------------------"

cd ./tools/user-repo

echo "---------------------USER REPOSITORY COMPILING...----------------------------------"
call mvn clean install
echo "---------------------USER REPOSITORY COMPILED.-------------------------------------"

echo "---------------------USER REPOSITORY STARTING...-----------------------------------"
call mvn spring-boot:run -Drun.profiles=h2
echo "---------------------USER REPOSITORY STOPPED....-----------------------------------"
