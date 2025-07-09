# Atlassian Simple MCP Server

If you cannot use the official version at https://www.atlassian.com/platform/remote-mcp-server, you can use this simple custom implementation!

Implemented with [Spring AI](https://spring.io/projects/spring-ai) using [Confluence Cloud API](https://developer.atlassian.com/cloud/confluence/rest/v3/intro/#about) and [Jira Cloud API](https://developer.atlassian.com/cloud/jira/platform/rest/v3/intro/#about)

## Requirements

* Java 21+

## Run locally

Go to your Atlassian account and create a new API token. You can find it in the "Security" section of your "Account settings".

Run:
```shell
export ATLASSIAN_URL=https://xxxx.atlassian.net
export ATLASSIAN_USER=xxxx
export ATLASSIAN_TOKEN=xxxx

./gradlew bootRun
```

Connect your favorite tool to the MCP server at `http://localhost:8888/sse`, for example for **VS Code** follow [Use MCP servers in VS Code](https://code.visualstudio.com/docs/copilot/chat/mcp-servers) and just create a `.vscode/mcp.json` configuration file like this:
```json
{
  "servers": {
    "local-tools": {
      "type": "sse",
      "url": "http://localhost:8888/sse"
    }
  }
}
```

## Sample prompts

* Check all the jira tickets from this month for project XXX and summarize each one with a short sentence
* On XXX confluence space, can you find the page that talks about xxx and summarize it?
