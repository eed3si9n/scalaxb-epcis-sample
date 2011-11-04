object Main extends App {
  import epcis._
  import scalaxb._
  import scala.xml.{NodeSeq, XML}
  
  val commissionXml = <epcis:EPCISDocument xmlns:epcis="urn:epcglobal:epcis:xsd:1"
        xmlns:core="urn:epcglobal:hls:1"
         schemaVersion="1" creationDate="2010-04-01T14:11:23Z">
         <EPCISBody>
           <EventList>
             <ObjectEvent>
               <eventTime>2010-04-01T08:00:01.001-06:00</eventTime>
               <eventTimeZoneOffset>-06:00</eventTimeZoneOffset>
               <epcList>
                 <epc>urn:epc:id:sgtin:0111222.999888.001</epc>
                 <epc>urn:epc:id:sgtin:0111222.999888.002</epc>
                 <epc>urn:epc:id:sgtin:0111222.999888.003</epc>
                 <epc>urn:epc:id:sgtin:0111222.999888.004</epc>
                 <epc>urn:epc:id:sgtin:0111222.999888.005</epc>
                 <epc>urn:epc:id:sgtin:0111222.999888.006</epc>
                 <epc>urn:epc:id:sgtin:0111222.999888.007</epc>
                 <epc>urn:epc:id:sgtin:0111222.999888.008</epc>
                 <epc>urn:epc:id:sgtin:0111222.999888.009</epc>
                 <epc>urn:epc:id:sgtin:0111222.999888.010</epc>
                 <epc>urn:epc:id:sgtin:0111222.999888.011</epc>
                 <epc>urn:epc:id:sgtin:0111222.999888.012</epc>
                 <epc>urn:epc:id:sgtin:0111222.999888.013</epc>
                 <epc>urn:epc:id:sgtin:0111222.999888.014</epc>
                 <epc>urn:epc:id:sgtin:0111222.999888.015</epc>
               </epcList>
               <action>ADD</action>
               <bizStep>urn:epcglobal:bizstep:commissioning</bizStep>
               <disposition>urn:epcglobal:disp:active</disposition>
               <readPoint>
                 <id>urn:epc:id:sgln:0111222.12345.40</id>
                 <core:gln>0111222123458</core:gln>
               </readPoint>
               <bizLocation>
                 <id>urn:epc:id:sgln:0111222.12345.10</id>
                 <core:gln>0111222123458</core:gln>
               </bizLocation>
             </ObjectEvent>
           </EventList>
         </EPCISBody>
       </epcis:EPCISDocument>
  
  // example1
  // example2
  testEventSequence2
  
  def testEventSequence2 {
    println("===testEventSequence2===")
    
    def toXML2(event: EPCISEventType): NodeSeq = {
      toXML[EPCISEventType](event, Some("urn:epcglobal:epcis:xsd:1"), Some("EventObject"), defaultScope)
    }
    val xmlObj = fromXML[EPCISDocumentType](commissionXml)
    val evtList = xmlObj.EPCISBody.EventList.get
    val evtListType = evtList.eventlisttypeoption
    val head = evtListType.head
    val evt = head.value
    val evtType = evt.asInstanceOf[EPCISEventType]
    val xmlNodeSeq = toXML2(evtType)
    val xmlStr = xmlNodeSeq.toString
    
    println("--xmlStr--")
    println(xmlStr)
    
    val retNodeSeq = XML.loadString(xmlStr)
    val retObj = fromXML[EPCISEventType](retNodeSeq)

    def translateToDataRec(evt:EPCISEventType) : DataRecord[Any] = {
      evt match {
        case e : ObjectEventType => DataRecord[ObjectEventType](e)
        case e : AggregationEventType => DataRecord[AggregationEventType](e)
        case e : TransactionEventType => DataRecord[TransactionEventType](e)
        case e : QuantityEventType => DataRecord[QuantityEventType](e)
      }
    }

    val event = translateToDataRec(retObj)
    val theEvtList = Some(EventListType(event))
    val body = EPCISBodyType(theEvtList, None, Nil, Map[String, scalaxb.DataRecord[Any]]())
    val eventDoc = EPCISDocumentType(None, body, None, Nil, 1, retObj.eventTime, Map[String, scalaxb.DataRecord[Any]]())

    val xml = toXML[EPCISDocumentType](eventDoc, Some("urn:epcglobal:epcis:xsd:1"), Some("EPCISDocument"), defaultScope)
    println("xml ="+xml) 
  }
  
  def testEventSequence {
    println("===testEventSequence===")
    
    val fixedScope = toScope(
       Some("epcis") -> "urn:epcglobal:epcis:xsd:1",
       Some("xs") -> "http://www.w3.org/2001/XMLSchema",
       Some("xsi") -> "http://www.w3.org/2001/XMLSchema-instance")
       
    def toXML3(event: EPCISDocumentType): NodeSeq = {
     toXML[EPCISDocumentType](event, Some("urn:epcglobal:epcis:xsd:1"), Some("EPCISDocument"), defaultScope)
    }

    def toXML2(event: EPCISEventType): NodeSeq = {
     toXML[EPCISEventType](event, Some("urn:epcglobal:epcis:xsd:1"), Some("EventObject"), fixedScope)
    }
    val xmlObj = fromXML[EPCISDocumentType](commissionXml)
    val evtList = xmlObj.EPCISBody.EventList.get
    val evtListType = evtList.eventlisttypeoption
    val head = evtListType.head
    val evt = head.value
    val evtType = evt.asInstanceOf[EPCISEventType]
    val xmlNodeSeq = toXML2(evtType)
    val xmlStr = xmlNodeSeq.toString

    val retNodeSeq = XML.loadString(xmlStr)
    val retObj = fromXML[EPCISEventType](retNodeSeq)

    val newNodeSeq = toXML2(retObj)
    val dr = DataRecord(None, Some("ObjectEvent"), None, None, retObj)
    val theNewEvtList = EventListType(dr)
    val resultingObj = xmlObj.copy(EPCISBody = xmlObj.EPCISBody.copy(EventList = Some(theNewEvtList)))
    val docNodeSeq = toXML3(resultingObj)
    val docXml = docNodeSeq.toString
    println("---docXml---")
    println(docXml)
    
    val newXmlStr = newNodeSeq.toString
    //try to read in the document to see if it is valid as well
    val newDocObj = fromXML[EPCISDocumentType](XML.loadString(docXml))
    val s = newXmlStr
  }
  
  def example1 {
    def toXML2(event: EPCISEventType): scala.xml.NodeSeq = {
     scalaxb.toXML[EPCISEventType](event, Some("urn:epcglobal:epcis:xsd:1"), Some("EventObject"), defaultScope)
    }
    val xmlObj = scalaxb.fromXML[EPCISDocumentType](commissionXml)
    val evtList = xmlObj.EPCISBody.EventList.get
    val evtListType = evtList.eventlisttypeoption
    val head = evtListType.head
    val evt = head.value
    val evtType = evt.asInstanceOf[EPCISEventType]
    val xmlNodeSeq = toXML2(evtType)
    val xmlStr = xmlNodeSeq.toString

    println("---xmlStr---")
    println(xmlStr)

    val retNodeSeq = scala.xml.XML.loadString(xmlStr)
    val retObj = scalaxb.fromXML[EPCISEventType](retNodeSeq)
    val newNodeSeq = toXML2(retObj)
    val newXmlStr = newNodeSeq.toString

    println("---newXmlStr---")
    val s = newXmlStr  
    println(newXmlStr)
  }
  
  def example2 {
    val voidXml = <epcis:EPCISDocument creationDate="2010-04-01T14:11:23Z" schemaVersion="1" xmlns:epcis="urn:epcglobal:epcis:xsd:1" xmlns:tnt="someNamespace" xmlns:core="urn:epcglobal:hls:1">
      <EPCISBody>
        <EventList>
          <ObjectEvent>
            <eventTime>2011-10-04T09:31:03.894-06:00</eventTime>
            <eventTimeZoneOffset>-06:00</eventTimeZoneOffset>
            <baseExtension>
              <tnt:voidEvent>4LiJJVevyKcYxvzs_FCJ-w</tnt:voidEvent>
            </baseExtension>
            <epcList>
              <epc>urn:epc:id:sgtin:0111222.999888.001</epc><epc>urn:epc:id:sgtin:0111222.999888.0010</epc><epc>urn:epc:id:sgtin:0111222.999888.0011</epc><epc>urn:epc:id:sgtin:0111222.999888.0012</epc><epc>urn:epc:id:sgtin:0111222.999888.002</epc><epc>urn:epc:id:sgtin:0111222.999888.003</epc><epc>urn:epc:id:sgtin:0111222.999888.004</epc><epc>urn:epc:id:sgtin:0111222.999888.005</epc><epc>urn:epc:id:sgtin:0111222.999888.006</epc><epc>urn:epc:id:sgtin:0111222.999888.007</epc><epc>urn:epc:id:sgtin:0111222.999888.008</epc><epc>urn:epc:id:sgtin:0111222.999888.009</epc>
            </epcList>
            <action>ADD</action>
            <bizStep>urn:epcglobal:bizstep:commissioning</bizStep>
            <disposition>urn:epcglobal:disp:active</disposition>
            <readPoint>
              <id>urn:epc:id:sgln:0111222.00001.0</id>
            </readPoint>
            <bizLocation>
              <id>urn:epc:id:sgln:0111222.00001.0</id>
            </bizLocation>
            <core:lot>xxx-100</core:lot>
            <core:expirationDate>2011-10-24T09:31:03.868-06:00</core:expirationDate>
            <core:productCode>urn:epc:idpat:sgtin:0111222.999888.*</core:productCode>
          </ObjectEvent>
        </EventList>
      </EPCISBody>
    </epcis:EPCISDocument>
            
    val xmlObj = fromXML[EPCISDocumentType](voidXml)
    val evtList = xmlObj.EPCISBody.EventList.get
    val evtListType = evtList.eventlisttypeoption
    val head = evtListType.head
    val evt = head.value
    val evtType = evt.asInstanceOf[EPCISEventType]
    val baseExt = evtType.baseExtension.get
    
    println(baseExt.toString)
    
    baseExt.any.size match {
      case 1 => 
      case _ => sys.error("should be size 1") 
    }
  }
}
