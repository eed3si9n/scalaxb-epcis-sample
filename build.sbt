import ScalaxbKeys._

organization := "com.example"

name := "scalaxb-epcis-sample"

seq(scalaxbSettings: _*)

packageName in scalaxb in Compile := "epcis"

sourceGenerators in Compile <+= scalaxb in Compile
