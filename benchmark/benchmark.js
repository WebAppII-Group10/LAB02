const loadtest = require('loadtest')
const fs = require('fs')
const jwt = require('jsonwebtoken')

//Const
const concurrency = 4
const token = jwt.sign({vz : ['A', 'B' ,'C'], exp: Math.floor(Date.now() / 1000) + (60 * 60)},'This is a very strong secret, do not share with no one!', { algorithm: 'HS384'})
//const token = "AAA.BBB.CCC"
const requestNumber = 10000
const hostURL = 'http://localhost:8080/API/verifyTicket'
const hostURLNoSub = 'http://localhost:8080/API/verifyTicketNoSub'



//parser
function parserOutput(src){
    return "\t\tTotal Request:" + src.totalRequests + "\n" + "\t\tTotal Error:" + src.totalErrors + "\n" + "\t\tTotal Duration:" + src.totalTimeSeconds + "\n" +
        "\t\tRPS:" + src.rps + "\n" + "\t\tMean Latency:" + src.meanLatencyMs + "\n" + "\t\tMax Latency:" + src.maxLatencyMs + "\n" +
        "\t\tMin Latency:" + src.minLatencyMs + "\n" + "\t\tPercentiles:" + JSON.stringify(src.percentiles) + "\n" + "\t\tError Code:" + JSON.stringify(src.errorCodes) + "\n"
        + "\t\tInstance Index:" + src.instanceIndex + "\n"
}

//Run program: verify with no sub
function loadTestExecuteVerifyTokenNoSub(){
            let obj =  {
                url: hostURLNoSub,
                maxRequests: requestNumber,
                concurrency: concurrency,
                method: 'POST',
                contentType : 'application/json',
                accept: 'application/json',
                body: {'zone' : 'A', 'token' : token}
            }
            loadtest.loadTest(obj, function(error, result) {
                if (error)
                {
                    return error
                }
                let tmp = parserOutput(result)
                fs.appendFileSync('./verifyTicketNoSub_REPORT.txt', tmp + "\n")
            })
}

//Run program: verify with subject
function loadTestExecuteVerifyToken(){
            let obj =  {
                url: hostURL,
                maxRequests: requestNumber,
                concurrency: concurrency,
                method: 'POST',
                contentType : 'application/json',
                accept: 'application/json',
                body: {'zone' : 'A', 'token' : token}
            }
            loadtest.loadTest(obj, function(error, result) {
                if (error)
                {
                    return error
                }
                let tmp = parserOutput(result)
                fs.appendFileSync('./verifyTicket_REPORT.txt', tmp + "\n")
            })

}

//Run program: verify with subject and keepalive agent
function loadTestExecuteVerifyTokenKeepAlive(){
            //console.log(content)
            let obj =  {
                url: hostURL,
                maxRequests: requestNumber,
                concurrency: concurrency,
                method: 'POST',
                contentType : 'application/json',
                accept: 'application/json',
                Connection: 'Keep-alive',
                body: {'zone' : 'A', 'token' : token}
            }

            loadtest.loadTest(obj, function(error, result) {
                if (error)
                {
                    return error
                }
                let tmp = parserOutput(result)
                fs.appendFileSync('./verifyTicketKeepAlive_REPORT.txt', tmp + "\n")
            })
}

function loadTestExecuteVerifyTokenNoSubKeepAlive(){
        //console.log(content)
        let obj =  {
            url: hostURLNoSub,
            maxRequests: requestNumber,
            concurrency: concurrency,
            method: 'POST',
            contentType : 'application/json',
            accept: 'application/json',
            Connection: 'Keep-alive',
            body: {'zone' : 'A', 'token' : token}
        }
        loadtest.loadTest(obj, function(error, result) {
            if (error)
            {
                return error
            }
            let tmp = parserOutput(result)
            fs.appendFileSync('./verifyTicketNoSubKeepAlive_REPORT.txt', tmp + "\n")
        })
}



console.log(token)
/**
 * NO SUB
 */
//loadTestExecuteVerifyTokenNoSub()
//loadTestExecuteVerifyTokenNoSubKeepAlive()
/**
 * STANDARD
 */
//loadTestExecuteVerifyToken()
//loadTestExecuteVerifyTokenKeepAlive()
/**
 * Command Line
 * loadtest http://localhost:8080/API/verifyTicket -n 10000 -H accpet:application/json -T application/json -P '{"zone":"A", "token":"AAA.BBB.CCC"}' -c 1
 */