//package com.example.demo.controller;
//
//import java.util.List;
//
//import org.springframework.data.mongodb.core.query.UpdateDefinition;
//
//public class Test {
//
//	public static void main(String[] args) {
//		List<UpdateDefinition> updateDefinitions = updates.stream() .map(updatePair -> Pair.of(updatePair.getFirst(), adaptToUpdateDefinition(updatePair.getSecond()))) .collect(Collectors.toList()); ops.updateMulti(updateDefinitions);
//
//		private UpdateDefinition adaptToUpdateDefinition(Update update) { return new UpdateDefinitionAdapter(update.getUpdateObject()); }
//	}
//
//}
