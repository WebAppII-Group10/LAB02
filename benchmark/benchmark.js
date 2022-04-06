const loadtest = require('loadtest')
const fs = require('fs')
const jwt = require('jsonwebtoken')

//Const
const concurrency = [8, 16, 32]
const token = jwt.sign({vz : ['A', 'B' ,'C'], exp: Math.floor(Date.now() / 1000) + (60 * 60)},'This is a very strong secret, do not share with no one!', { algorithm: 'HS384'})
const requestNumber = 10000
const hostURL = 'http://localhost:8080/API/verifyTicket'


//parser
function parserOutput(src){
    return "\t\tTotal Request:" + src.totalRequests + "\n" + "\t\tTotal Error:" + src.totalErrors + "\n" + "\t\tTotal Duration:" + src.totalTimeSeconds + "\n" +
        "\t\tRPS:" + src.rps + "\n" + "\t\tMean Latency:" + src.meanLatencyMs + "\n" + "\t\tMax Latency:" + src.maxLatencyMs + "\n" +
        "\t\tMin Latency:" + src.minLatencyMs + "\n" + "\t\tPercentiles:" + JSON.stringify(src.percentiles) + "\n" + "\t\tError Code:" + JSON.stringify(src.errorCodes) + "\n"
        + "\t\tInstance Index:" + src.instanceIndex + "\n"
}

//Run program: verify with no sub
function loadTestExecuteVerifyTokenNoSub(){
    fs.writeFileSync('./verifyTicketNoSub_REPORT.txt', "************VERIFY TOKEN************\n")
        concurrency.forEach(conc => {
            //console.log(content)
            let obj =  {
                url: hostURL,
                maxRequests: requestNumber,
                concurrency: conc,
                method: 'POST',
                contentType : 'application/json',
                accept: 'application/json',
                body: {'zone' : 'A', 'token' : token}
            }

            console.log(obj)
            loadtest.loadTest(obj, function(error, result) {
                if (error)
                {
                    return error
                }
                //console.log(result.totalRequests)
                fs.appendFileSync('./verifyTicketNoSub_REPORT.txt', "\tNumber of Threads: " + conc + "\n")
                let tmp = parserOutput(result)
                fs.appendFileSync('./verifyTicketNoSub_REPORT.txt', tmp + "\n")
            })
        })
}

//Run program: verify with subject
async function loadTestExecuteVerifyToken(){
    fs.writeFileSync('./verifyTicket_REPORT.txt', "************VERIFY TOKEN************\n")
        for (let conc of concurrency){
            //console.log(content)
            let obj =  {
                url: hostURL,
                maxRequests: requestNumber,
                concurrency: conc,
                method: 'POST',
                contentType : 'application/json',
                accept: 'application/json',
                body: {'zone' : 'A', 'token' : token}
            }

            console.log(obj)
            await loadtest.loadTest(obj, function(error, result) {
                if (error)
                {
                    return error
                }
                //console.log(result.totalRequests)
                fs.appendFileSync('./verifyTicket_REPORT.txt', "\tNumber of Threads: " + conc + "\n")
                let tmp = parserOutput(result)
                fs.appendFileSync('./verifyTicket_REPORT.txt', tmp + "\n")
            })
        }
}

//Run program: verify with subject and keepalive agent
function loadTestExecuteVerifyTokenKeepAlive(){
    fs.writeFileSync('./verifyTicketKeepAlive_REPORT.txt', "************VERIFY TOKEN************\n")
        concurrency.forEach(conc => {
            //console.log(content)
            let obj =  {
                url: hostURL,
                maxRequests: requestNumber,
                concurrency: conc,
                method: 'POST',
                contentType : 'application/json',
                accept: 'application/json',
                body: {'zone' : 'A', 'token' : token}
            }

            console.log(obj)
            loadtest.loadTest(obj, function(error, result) {
                if (error)
                {
                    return error
                }
                //console.log(result.totalRequests)
                fs.appendFileSync('./verifyTicketKeepAlive_REPORT.txt', "\tNumber of Threads: " + conc + "\n")
                let tmp = parserOutput(result)
                fs.appendFileSync('./verifyTicketKeepAlive_REPORT.txt', tmp + "\n")
            })
        })
}


//loadTestExecuteVerifyTokenNoSub()
loadTestExecuteVerifyToken()
//loadTestExecuteVerifyTokenKeepAlive()