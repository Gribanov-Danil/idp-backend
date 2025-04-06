<flow-definition plugin="workflow-job@2.40">
    <description>${jobName!''}</description>
    <keepDependencies>false</keepDependencies>
    <properties/>
    <definition class="org.jenkinsci.plugins.workflow.cps.CpsScmFlowDefinition" plugin="workflow-cps@2.94">
        <scm class="hudson.plugins.git.GitSCM" plugin="git@4.11.3">
            <configVersion>2</configVersion>
            <userRemoteConfigs>
                <hudson.plugins.git.UserRemoteConfig>
                    <url>${repoUrl!''}</url>
                </hudson.plugins.git.UserRemoteConfig>
            </userRemoteConfigs>
            <branches>
                <hudson.plugins.git.BranchSpec>
                    <name>*/master</name>
                </hudson.plugins.git.BranchSpec>
            </branches>
            <doGenerateSubmoduleConfigurations>false</doGenerateSubmoduleConfigurations>
            <submoduleCfg class="empty-list"/>
            <extensions/>
        </scm>
        <scriptPath>${jenkinsfilePath!''}</scriptPath>
        <lightweight>true</lightweight>
    </definition>
    <triggers/>
    <disabled>false</disabled>
</flow-definition>
