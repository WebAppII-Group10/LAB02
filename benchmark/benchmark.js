const loadtest = require('loadtest')
const fs = require("fs")
const jwt = require('jsonwebtoken')

//Data Source for testing
const testGetURLsOK = ["http://93.146.161.198:80/API/generateTicket?zoneId=1",
    //"http://192.168.1.5:8080/API/generateTicketNoSub?zoneId=1"
]

const testPostURLsOK = ["http://93.146.161.198:80/API/verifyTicketNoSub",
    //"http://192.168.1.5:8080/API/verifyTicket",
]

const concurrency = [32]

const requestNumber = 10000

//parser
function parserOutput(src){
    return "\t\tTotal Request:" + src.totalRequests + "\n" + "\t\tTotal Error:" + src.totalErrors + "\n" + "\t\tTotal Duration:" + src.totalTimeSeconds + "\n" +
         "\t\tRPS:" + src.rps + "\n" + "\t\tMean Latency:" + src.meanLatencyMs + "\n" + "\t\tMax Latency:" + src.maxLatencyMs + "\n" +
        "\t\tMin Latency:" + src.minLatencyMs + "\n" + "\t\tPercentiles:" + JSON.stringify(src.percentiles) + "\n" + "\t\tError Code:" + JSON.stringify(src.errorCodes) + "\n"
        + "\t\tInstance Index:" + src.instanceIndex + "\n"
}

//Run program: entry point
function loadTestExecuteGenrateTokenNoSub(){
    //GET: generateToken
    fs.writeFileSync('./_REPORT.txt', "************GENERATE TOKEN************\n")
    testGetURLsOK.forEach(path => {
        fs.appendFileSync('./generateTicket_REPORT.txt',"PATH: " + path + "\n")
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
                fs.appendFileSync('./generateTicket_REPORT.txt', "\tNumber of Threads: " + conc + "\n")
                let tmp = parserOutput(result)
                fs.appendFileSync('./generateTicket_REPORT.txt', tmp + "\n")
            })
        })
    })
}


//Run program: entry point
function loadTestExecuteVerifyToken(){
    //GET: generateToken
    let token = jwt.sign({vz : ['A', 'B' ,'C'], exp: Math.floor(Date.now() / 1000) + (60 * 60), sub : 'TICKET_1'},'This is a very strong secret, do not share with no one!', { algorithm: 'HS384'})
    console.log(token)
    //fs.writeFileSync('./verifyTicket_REPORT.txt', "************VERIFY TOKEN************\n")
        //fs.appendFileSync('./verifyTicket_REPORT.txt',"PATH: http://93.146.161.198:80/API/verifyTicket\n")
        concurrency.forEach(conc => {
            //console.log(content)
            let obj =  {
                url: 'http://93.146.161.198:80/API/verifyTicket',
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
                fs.appendFileSync('./verifyTicket_REPORT.txt', "\tNumber of Threads: " + conc + "\n")
                let tmp = parserOutput(result)
                fs.appendFileSync('./verifyTicket_REPORT.txt', tmp + "\n")
            })
        })
  }

  //Run program: entry point
  function loadTestExecuteVerifyTokenNoSub(){
      //GET: generateToken
      let token = jwt.sign({vz : ['A', 'B' ,'C'], exp: Math.floor(Date.now() / 1000) + (60 * 60)},'This is a very strong secret, do not share with no one!', { algorithm: 'HS384'})
      console.log(token)
      fs.writeFileSync('./verifyTicketNoSub_REPORT.txt', "************VERIFY TOKEN************\n")
      testPostURLsOK.forEach(path => {
          fs.appendFileSync('./verifyTicketNoSub_REPORT.txt',"PATH: " + path + "\n")
          concurrency.forEach(conc => {
              //console.log(content)
              let obj =  {
                  url: path,
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
      })
    }


//loadTestExecuteVerifyTokenNoSub()
loadTestExecuteVerifyToken()
