const loadtest = require('loadtest');
const fs = require("fs")

//Data Source for testing
const testGetURLsOK = ["http://localhost:8080/API/generateTicket?zoneId=1",
    //"http://192.168.1.5:8080/API/generateTicketNoSub?zoneId=1"
]

const testPostURLsOK = ["http://192.168.1.5:8080/API/verifyTicket",
    "http://192.168.1.5:8080/API/verifyTicket",
]

const concurrency = [1, 2, 4, 6, 8, 12, 16, 24, 32]

const requestNumber = 10000


//parser
function parserOutput(src){
    return "\t\tTotal Request:" + src.totalRequests + "\n" + "\t\tTotal Error:" + src.totalErrors + "\n" + "\t\tTotal Duration:" + src.totalTimeSeconds + "\n" +
         "\t\tRPS:" + src.rps + "\n" + "\t\tMean Latency:" + src.meanLatencyMs + "\n" + "\t\tMax Latency:" + src.maxLatencyMs + "\n" +
        "\t\tMin Latency:" + src.minLatencyMs + "\n" + "\t\tPercentiles:" + JSON.stringify(src.percentiles) + "\n" + "\t\tError Code:" + JSON.stringify(src.errorCodes) + "\n"
        + "\t\tInstance Index:" + src.instanceIndex + "\n"
}

//Run program: entry point
function loadTestExecute(){
    //GET: generateToken
    fs.writeFileSync('./_REPORT.txt', "************GENERATE TOKEN************\n")
    testGetURLsOK.forEach(path => {
        fs.appendFileSync('./_REPORT.txt',"PATH: " + path + "\n")
        concurrency.forEach(conc => {
            //console.log(content)
            let obj =  {
                url: path,
                maxRequests: requestNumber,
                concurrency: conc
            }
            //console.log(obj)
            loadtest.loadTest(obj, function(error, result) {
                if (error)
                {
                    return error
                }
                //console.log(result.totalRequests)
                fs.appendFileSync('./_REPORT.txt', "\tNumber of Threads: " + conc + "\n")
                let tmp = parserOutput(result)
                fs.appendFileSync('./_REPORT.txt', tmp + "\n")
            })
        })
    })
}


loadTestExecute()


