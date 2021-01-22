for i in range(1,10):
    print(f"java winzigc -ast winzig_test_programs/winzig_0{i} > tree.0{i}")
    print(f"diff tree.0{i} winzig_test_programs/winzig_0{i}.tree")

for i in range(10,16):
    print(f"java winzigc -ast winzig_test_programs/winzig_{i} > tree.{i}")
    print(f"diff tree.{i} winzig_test_programs/winzig_{i}.tree")

