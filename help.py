for i in range(1,10):
    print(f"java winzigc -ast winzig_test_programs/winzig_0{i} > tree.0{i}")
    print(f"diff tree.0{i} winzig_test_programs/winzig_0{i}.tree")

for i in range(10,16):
    print(f"java winzigc -ast winzig_test_programs/winzig_{i} > tree.{i}")
    print(f"diff tree.{i} winzig_test_programs/winzig_{i}.tree")


javac *.java
java winzigc -ast winzig_test_programs/winzig_01 > tree.01
diff tree.01 winzig_test_programs/winzig_01.tree
java winzigc -ast winzig_test_programs/winzig_02 > tree.02
diff tree.02 winzig_test_programs/winzig_02.tree
java winzigc -ast winzig_test_programs/winzig_03 > tree.03
diff tree.03 winzig_test_programs/winzig_03.tree
java winzigc -ast winzig_test_programs/winzig_04 > tree.04
diff tree.04 winzig_test_programs/winzig_04.tree
java winzigc -ast winzig_test_programs/winzig_05 > tree.05
diff tree.05 winzig_test_programs/winzig_05.tree
java winzigc -ast winzig_test_programs/winzig_06 > tree.06
diff tree.06 winzig_test_programs/winzig_06.tree
java winzigc -ast winzig_test_programs/winzig_07 > tree.07
diff tree.07 winzig_test_programs/winzig_07.tree
java winzigc -ast winzig_test_programs/winzig_08 > tree.08
diff tree.08 winzig_test_programs/winzig_08.tree
java winzigc -ast winzig_test_programs/winzig_09 > tree.09
diff tree.09 winzig_test_programs/winzig_09.tree
java winzigc -ast winzig_test_programs/winzig_10 > tree.10
diff tree.10 winzig_test_programs/winzig_10.tree
java winzigc -ast winzig_test_programs/winzig_11 > tree.11
diff tree.11 winzig_test_programs/winzig_11.tree
java winzigc -ast winzig_test_programs/winzig_12 > tree.12
diff tree.12 winzig_test_programs/winzig_12.tree
java winzigc -ast winzig_test_programs/winzig_13 > tree.13
diff tree.13 winzig_test_programs/winzig_13.tree
java winzigc -ast winzig_test_programs/winzig_14 > tree.14
diff tree.14 winzig_test_programs/winzig_14.tree
java winzigc -ast winzig_test_programs/winzig_15 > tree.15
diff tree.15 winzig_test_programs/winzig_15.tree
