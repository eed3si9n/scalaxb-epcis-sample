object Main extends App {
  import epcis._
  
  val xml = <ObjectEvent
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xmlns:epcis="urn:epcglobal:epcis:xsd:1"
      xsi:type="epcis:ObjectEventType">
    <eventTime>2010-01-01</eventTime>
    <eventTimeZoneOffset>Z</eventTimeZoneOffset>
    <epcList/>
    <action>ADD</action>
  </ObjectEvent>
  
  val normalizedEvent: EPCISEventType = scalaxb.fromXML[EPCISEventType](xml)
  println(normalizedEvent.toString)
  
  val temp = scalaxb.toXML(normalizedEvent, None, "ObjectType", defaultScope)
  println(temp)
  
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
   
   println(xmlStr)
   
   val retNodeSeq = scala.xml.XML.loadString(xmlStr)
   val retObj = scalaxb.fromXML[EPCISEventType](retNodeSeq)
   val newNodeSeq = toXML2(retObj)
   val newXmlStr = newNodeSeq.toString

   val s = newXmlStr  
}
