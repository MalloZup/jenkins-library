// Copyright 2017 SUSE LINUX GmbH, Nuernberg, Germany.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
import com.suse.kubic.Environment

def call(Map parameters = [:]) {
    Environment environment = parameters.get('environment')

    writeFile(file: "${env.WORKSPACE}/ssh_config", text: """
Host 10.17.3.*
     User ${environment.sshUser}
     IdentityFile ${environment.sshKey}
     UserKnownHostsFile /dev/null
     StrictHostKeyChecking no
"""
)

    dir("testinfra"){
        createPythonVenv(name: "testinfra")
        inPythonVenv(name: "testinfra", script:"pip install -r requirements.txt")
        environment.minions.each { minion ->
            inPythonVenv(name:"testinfra", script:"pytest --ssh-config=${env.WORKSPACE}/ssh_config --sudo --hosts=${minion.ipv4} -m \"${minion.role} or common\" --junit-xml ${minion.role}-${minion.id}.xml -v | tee -a ${env.WORKSPACE}/logs/testinfra.log")
        }
        junit '*.xml'
    }
}
